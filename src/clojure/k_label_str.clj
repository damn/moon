(ns clojure.k-label-str)

; INLIME

(defn k->label-str [k]
  (str "[LIGHT_GRAY]:"
       (when-let [ns (namespace k)] (str ns "/"))
       "[][WHITE]"
       (name k)
       "[]"))
