(ns create.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.file.pixmap :as file-handle->pixmap]
            [pixmap.dispose :as dispose]
            [clojure.files :as files]
            [clojure.graphics.new-cursor :as new-cursor]))

(defn step
  [{:keys [ctx/files
           ctx/graphics]}]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap (file-handle->pixmap/f (files/internal files path))
                         cursor (new-cursor/f graphics pixmap hotspot-x hotspot-y)]
                     (dispose/f! pixmap)
                     cursor)))))
