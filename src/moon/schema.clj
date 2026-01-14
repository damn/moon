(ns moon.schema)

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defmulti malli-form (fn [[k] _schemas]
                       k))
