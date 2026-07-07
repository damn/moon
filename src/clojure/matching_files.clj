(ns clojure.matching-files
  (:require [clojure.java.io :as io]))

(defn f [patterns]
  (->> patterns
       (mapcat #(file-seq (io/file %)))
       (filter java.io.File/.isFile)))
