(ns moon.application.create.textures
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn step
  [{:keys [^Application ctx/app]
    :as ctx}]
  (assoc ctx :ctx/textures
         (into {} (for [path (map (fn [path]
                                    (str/replace-first path folder ""))
                                  (loop [[^FileHandle file & remaining] (.list (.internal (.getFiles app) folder))
                                         result []]
                                    (cond (nil? file)
                                          result

                                          (.isDirectory file)
                                          (recur (concat remaining (.list file)) result)

                                          (extensions (.extension file))
                                          (recur remaining (conj result (.path file)))

                                          :else
                                          (recur remaining result))))]
                    [path (Texture. path)]))))
