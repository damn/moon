(ns moon.impl.textures
  (:require [clojure.files :as files]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.string :as str])
  (:import (com.badlogic.gdx.files FileHandle)))

(defn- recursively-search
  [^FileHandle folder extensions]
  (loop [[^FileHandle file & remaining] (.list folder)
         result []]
    (cond (nil? file)
          result

          (.isDirectory file)
          (recur (concat remaining (.list file)) result)

          (extensions (.extension file))
          (recur remaining (conj result (.path file)))

          :else
          (recur remaining result))))

(defn create [{:keys [ctx/files]} {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search (files/internal files folder) extensions))]
             [path (texture/create path)])))
