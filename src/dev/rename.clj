(ns dev.rename
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
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
  ; vimgrep/ns dev/gj src/** resources/**/*.edn test/** project.clj
  (let [files (matching-files ["src" "resources" "test"])]
    (doseq [f files]
      (replace-in-file! f from to))))

(comment
 (let [from "clojure"
       to   "moon"]
   (rename! from to)
   )
 )

(defn move-file! [source-path target-path]
  (Files/move
   (Paths/get source-path (make-array String 0))
   (Paths/get target-path (make-array String 0))
   (into-array StandardCopyOption [StandardCopyOption/REPLACE_EXISTING])))

(defn ns->file [ns-string]
  (str "src/"
       (-> ns-string
           (str/replace "." "/")
           (str/replace "-" "_"))
       ".clj"))

(defn move-and-rename! [from-ns to-ns]
  (let [from-file (ns->file from-ns)
        to-file   (ns->file to-ns)]
    (println "Moving file " from-file " to " to-file)
    (move-file! from-file to-file))
  (println "Renaming " from-ns " to " to-ns)
  (rename! from-ns
           to-ns))

(comment

 (let [suffix "vector"]
   (rename! (str "clojure." suffix ".")
            (str suffix ".")))

 (rename! "gdl.stage."
          "scene2d.stage."
          )


     (doseq [ns-string [
                        "button-group"
                        "cell"
                        "check-box"
                        "horizontal-group"
                        "image"
                        "image-button"
                        "label"
                        "scroll-pane"
                        "select-box"
                        "skin"
                        "stack"
                        "table"
                        "text-button"
                        "text-field"
                        "text-tooltip"
                        "tooltip-manager"
                        "widget"
                        "window"]]
       (rename! (str "gdl." ns-string)
                (str "ui." ns-string))
       )


 (let [suffix "actor"
       prefix-a "clojure."
       prefix-b "gdl."]
   (rename! "gdl.actor."
            "gdl."))
 (move-and-rename! "moon.scene2d.group"
                   "gdx.scenes.scene2d.group")




 )
