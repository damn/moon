(ns editor.create-widget)

(defmulti f
  (fn [[schema-k :as _schema] v ctx]
    schema-k))
