(ns gdx.textures
  (:require [clojure.string :as str]
            [clojure.gdx.files :as files]
            [clojure.gdx.files.file-handle :as file]
            [clojure.gdx.graphics.texture :as texture]))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn create
  [files]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[file & remaining] (file/list (files/internal files folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (file/directory? file)
                                   (recur (concat remaining (file/list file)) result)

                                   (extensions (file/extension file))
                                   (recur remaining (conj result (file/path file)))

                                   :else
                                   (recur remaining result))))]
             [path (texture/create (files/internal files path))])))
