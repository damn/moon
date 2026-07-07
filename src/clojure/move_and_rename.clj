(ns clojure.move-and-rename
  (:require [clojure.move-file :as move-file]
            [clojure.ns-to-file :as ns-to-file]
            [clojure.rename :as rename]))

(defn f [from-ns to-ns]
  (let [from-file (ns-to-file/f from-ns)
        to-file   (ns-to-file/f to-ns)]
    (println "Moving file " from-file " to " to-file)
    (move-file/f from-file to-file))
  (println "Renaming " from-ns " to " to-ns)
  (rename/f from-ns to-ns))
