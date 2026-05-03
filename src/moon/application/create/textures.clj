(ns moon.application.create.textures
  (:require [clojure.string :as str]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.files.file-handle :as file-handle]
            [com.badlogic.gdx.graphics.texture :as texture]))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/textures
         (into {} (for [path (map (fn [path]
                                    (str/replace-first path folder ""))
                                  (loop [[file & remaining] (file-handle/list (files/internal (app/files app) folder))
                                         result []]
                                    (cond (nil? file)
                                          result

                                          (file-handle/directory? file)
                                          (recur (concat remaining (file-handle/list file)) result)

                                          (extensions (file-handle/extension file))
                                          (recur remaining (conj result (file-handle/path file)))

                                          :else
                                          (recur remaining result))))]
                    [path (texture/create path)]))))
