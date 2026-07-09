(ns clojure.java.nio.file.files
  (:import (java.nio.file Files
                          Paths
                          StandardCopyOption)))

(defn move [source-path target-path]
  (Files/move
   (Paths/get source-path (make-array String 0))
   (Paths/get target-path (make-array String 0))
   (into-array StandardCopyOption [StandardCopyOption/REPLACE_EXISTING])))
