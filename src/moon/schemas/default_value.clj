(ns moon.schemas.default-value)

(defn default-value [schemas k]
  (let [schema (get schemas k)]
    (cond
     (#{:s/map} (schema 0)) {}
     :else nil)))
