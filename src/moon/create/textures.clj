(ns moon.create.textures
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx Files) ; TODO load as edn assets before for deploy also
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

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

(defn- search [^Files files {:keys [folder extensions]}]
  (map (fn [path]
         (str/replace-first path folder ""))
       (recursively-search (.internal files folder) extensions)))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   folder]
  (assoc ctx :ctx/textures
         (into {} (for [path (search files folder)]
                    [path (Texture. ^String path)]))))
