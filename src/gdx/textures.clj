(ns gdx.textures
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn create
  [^Files files]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[^FileHandle file & remaining] (.list (.internal files folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (.isDirectory file)
                                   (recur (concat remaining (.list file)) result)

                                   (extensions (.extension file))
                                   (recur remaining (conj result (.path file)))

                                   :else
                                   (recur remaining result))))]
             [path (Texture. (.internal files path))])))
