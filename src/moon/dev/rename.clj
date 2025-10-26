(ns moon.dev.rename
  (:require [clojure.java.io :as io])
  (:import (java.nio.file Files
                          Paths
                          StandardCopyOption)))

(defn replace-in-file! [^java.io.File file from to]
  (let [content (slurp file)
        new-content (.replaceAll content (java.util.regex.Pattern/quote from) to)]
    (when (not= content new-content)
      (spit file new-content)
      (println "Updated:" (.getPath file)))))

(defn matching-files [patterns]
  (->> patterns
       (mapcat #(file-seq (io/file %)))
       (filter java.io.File/.isFile)))

(defn rename! [from to]
  (let [files (matching-files ["src" "resources" "test"])]
    (doseq [f files]
      (replace-in-file! f from to))))

(comment
 (let [from "clojure.scene2d.build"
       to   "cdq.ui.build"]
   (rename! from to)
   )
 )

(defn move-file [source-path target-path]
  (Files/move
   (Paths/get source-path (make-array String 0))
   (Paths/get target-path (make-array String 0))
   (into-array StandardCopyOption [StandardCopyOption/REPLACE_EXISTING])))

(comment
 ; Move ns
 ; clojure.scene2d.vis-ui.check-box
 ; to
 ; cdq.ui.check-box

 ; 1. move file
 ; 2. rename

 (let [from-ns 'clojure.scene2d.vis-ui.check-box
       to-ns 'cdq.ui.check-box]
   (move-file "src/clojure/scene2d/vis_ui/text_button.clj"
              "src/cdq/ui/text_button.clj")
   (rename! "clojure.scene2d.vis-ui.text-button"
            "cdq.ui.text-button")
   )
 )
