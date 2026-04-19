(ns clojure.app-values
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :as str]))

(defn get-namespaces [packages]
  (filter #(packages (first (str/split (name (ns-name %)) #"\.")))
          (all-ns)))

(defn get-vars [nmspace condition]
  (for [[sym avar] (ns-interns nmspace)
       :when (condition avar)]
    avar))

(defn- protocol? [value]
  (and (instance? clojure.lang.PersistentArrayMap value)
       (:on value)))

(defn- non-function? [value]
  (not (or (fn? value)
           (instance? clojure.lang.MultiFn value)
           (protocol? value)
           (instance? java.lang.Class value))))

(defn- get-non-fn-vars [nmspace]
  (get-vars nmspace (fn [avar]
                      (non-function? @avar))))

(defn ns-value-vars [nmspace]
  (for [value-vars (get-non-fn-vars nmspace)
        :when (seq value-vars)]
    [(ns-name nmspace) value-vars]))

(defn package-value-vars
  "Returns a map of ns-name to value-vars (non-function vars).
  Use to understand the state of your application.

  Example: `(package-value-vars #{\"forge\"})`"
  [packages]
  (into {} (map ns-value-vars (get-namespaces packages))))

(defn print-app-values-tree [file namespaces-set]
  (spit file
        (with-out-str
         (pprint
          (for [[ns-name vars] (package-value-vars namespaces-set)]
            [ns-name (map #(:name (meta %)) vars)])))))
