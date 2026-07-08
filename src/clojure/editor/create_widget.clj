(ns clojure.editor.create-widget)

(defmulti create-widget
  (fn [[schema-k :as _schema] v ctx]
    schema-k))
