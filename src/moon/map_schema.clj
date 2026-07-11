(ns moon.map-schema)

(defn map-form-k->properties
  "Given a map schema gives a map of key to key properties (like :optional)."
  [map-schema]
  (let [[_m _p & ks] map-schema]
    (into {} (for [[k m? _schema] ks]
               [k (if (map? m?) m?)]))))

(defn map-keys [map-schema]
  (let [[_m _p & ks] map-schema]
    (for [[k m? _schema] ks]
      k)))

(defn optional? [map-schema k]
  (:optional (k (map-form-k->properties map-schema))))

(defn optional-keyset [map-schema]
  (set (filter #(optional? map-schema %)
               (map-keys map-schema))))
