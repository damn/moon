(ns moon.application.create.textures
  (:require [clojure.string :as str]
            [moon.files :as files])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn step
  [ctx]
  (assoc ctx :ctx/textures
         (into {} (for [path (map (fn [path]
                                    (str/replace-first path folder ""))
                                  (loop [[^FileHandle file & remaining] (.list ^FileHandle (files/internal ctx folder))
                                         result []]
                                    (cond (nil? file)
                                          result

                                          (.isDirectory file)
                                          (recur (concat remaining (.list file)) result)

                                          (extensions (.extension file))
                                          (recur remaining (conj result (.path file)))

                                          :else
                                          (recur remaining result))))]
                    [path (Texture. ^String path)]))))
