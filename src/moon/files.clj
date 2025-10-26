(ns moon.files
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.files FileHandle)))

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

(defn search [files {:keys [folder extensions]}]
  (map (fn [path]
         (str/replace-first path folder ""))
       (recursively-search (Files/.internal files folder) extensions)))
