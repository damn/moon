(ns moon.schema.widget-value)

(defmulti f
  (fn [[schema-k :as _schema] widget schemas]
    schema-k))
