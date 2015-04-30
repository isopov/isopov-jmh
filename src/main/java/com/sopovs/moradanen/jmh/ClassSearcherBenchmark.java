package com.sopovs.moradanen.jmh;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.FieldValueQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class ClassSearcherBenchmark {

    private static final int SIZE = 100000;

    public static void main(String[] args) {
        String[] names = new String[SIZE];
        long[] dates = new long[SIZE];
        Random random = new Random(228L);
        for (int i = 0; i < SIZE; i++) {
            names[i] = UUID.randomUUID().toString();
            dates[i] = random.nextLong();
        }

        long start = System.nanoTime();
        ISearcher searcher = new DumbClassSearcher();
        searcher.refresh(names, dates);
        System.out.println(System.nanoTime() - start);

        System.out.println(searcher.guess("abcd").length);
    }

    public static class LuceneClassSearcher implements ISearcher {
        final Directory index = new RAMDirectory();
        final StandardAnalyzer analyzer = new StandardAnalyzer();
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);

        final FieldType dataType = new FieldType(LongField.TYPE_STORED);
        final FieldType nameType = new FieldType();

        public LuceneClassSearcher() {
            dataType.setDocValuesType(DocValuesType.NUMERIC);
            dataType.freeze();

            nameType.setIndexOptions(IndexOptions.DOCS);
            nameType.setStored(true);
            // nameType.setDocValuesType(DocValuesType.SORTED);
            nameType.freeze();
        }

        @Override
        public void refresh(String[] classNames, long[] modificationDates) {
            try (IndexWriter writer = new IndexWriter(index, config)) {
                for (int i = 0; i < classNames.length; i++) {
                    Document doc = new Document();
                    doc.add(new TextField("name", classNames[i], Store.YES));
                    doc.add(new LongField("date", modificationDates[i], dataType));
                    writer.addDocument(doc);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public String[] guess(String start) {

            try (IndexReader reader = DirectoryReader.open(index)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                Query query = new FieldValueQuery(start);
                // Query query = new TermQuery(new Term("name", start));
                Sort sort = new Sort(new SortField("date", SortField.Type.LONG, true)
                        // ,
                        // new SortField("name", SortField.Type.STRING)
                        );
                TopDocs docs = searcher.search(query, 10, sort);

                String[] result = new String[docs.scoreDocs.length];
                for (int i = 0; i < result.length; i++) {
                    Document doc = reader.document(docs.scoreDocs[i].doc);
                    result[i] = doc.get("name");
                }
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Very simple searcher - instant guessing, several seconds refreshing and
     * using tons of memory (1GB of heap is required for 100000 class names)
     *
     */
    public static class DumbClassSearcher implements ISearcher {
        private static final String[] EMPTY = new String[0];

        private Map<String, SortedSet<ClassEntry>> data = new HashMap<>(100000);

        @Override
        public void refresh(String[] classNames, long[] modificationDates) {
            for (int i = 0; i < modificationDates.length; i++) {
                ClassEntry entry = new ClassEntry(classNames[i], modificationDates[i]);
                for (int j = 1; j <= entry.getClassName().length(); j++) {
                    String key = entry.getClassName().substring(0, j);
                    SortedSet<ClassEntry> dataSet = data.get(key);
                    if (dataSet == null) {
                        dataSet = new TreeSet<>();
                        data.put(key, dataSet);
                    }
                    dataSet.add(entry);
                    if (dataSet.size() > 10) {
                        dataSet.remove(dataSet.last());
                    }
                }
            }
        }

        @Override
        public String[] guess(String start) {
            SortedSet<ClassEntry> dataSet = data.get(start);
            if (dataSet == null) {
                return EMPTY;
            }
            String[] result = new String[dataSet.size()];
            int counter = 0;
            for (ClassEntry entry : dataSet) {
                result[counter++] = entry.getClassName();
            }
            return result;
        }
    }

    private static class ClassEntry implements Comparable<ClassEntry> {
        private final String className;
        private final long modificationDate;
        private static final Comparator<ClassEntry> COMPARATOR = Comparator
                .comparingLong(ClassEntry::getModificationDate)
                .reversed()
                .thenComparing(ClassEntry::getClassName);

        public ClassEntry(String className, long modificationDate) {
            this.className = className;
            this.modificationDate = modificationDate;
        }

        public String getClassName() {
            return className;
        }

        public long getModificationDate() {
            return modificationDate;
        }

        @Override
        public int compareTo(ClassEntry o) {
            return COMPARATOR.compare(this, o);
        }
    }

    public interface ISearcher {
        public void refresh(String[] classNames, long[] modificationDates);

        public String[] guess(String start);
    }

}
