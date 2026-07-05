(ns clojure.malli-form)

(defn malli-form [schema schemas]
  (let [k->malli-form (:schemas/k->malli-form (meta schemas))]
    ((k->malli-form (first schema)) schema schemas)))
