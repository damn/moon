(ns clojure.file
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File)))

(defn list-files [folder extension]
  (let [^File file (io/file folder)]
    (->> (.listFiles file)
         (filter File/.isFile)
         (filter #(str/ends-with? (File/.getName %) extension))
         (map File/.getName))))
