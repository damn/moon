(ns moon.impl.textures
  (:require [clojure.gdx.app :as app]
            [clojure.gdx.files :as files]
            [clojure.string :as str])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn create
  [{:keys [ctx/app]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[^FileHandle file & remaining] (.list ^FileHandle (files/internal (app/files app) folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (.isDirectory file)
                                   (recur (concat remaining (.list file)) result)

                                   (extensions (.extension file))
                                   (recur remaining (conj result (.path file)))

                                   :else
                                   (recur remaining result))))]
             [path (Texture. ^String path)])))
