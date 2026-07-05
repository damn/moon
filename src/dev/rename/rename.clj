(ns dev.rename.rename
  (:require [dev.rename.matching-files :as matching-files]
            [dev.rename.replace-in-file :as replace-in-file]))

(defn f [from to]
  (doseq [file (matching-files/f ["src" "resources" "test"])]
    (replace-in-file/f file from to)))
