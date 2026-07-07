(ns clojure.rename
  (:require [clojure.matching-files :as matching-files]
            [clojure.replace-in-file :as replace-in-file]))

(defn f [from to]
  (doseq [file (matching-files/f ["src" "resources" "test"])]
    (replace-in-file/f file from to)))
