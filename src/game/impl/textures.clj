(ns game.impl.textures
  (:require [clojure.gdx.app :as app]
            [clojure.gdx.files :as files]
            [clojure.string :as str]
            [com.badlogic.gdx.files.file-handle :as file]
            [com.badlogic.gdx.graphics.texture :as texture]))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn create
  [{:keys [ctx/app]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[file & remaining] (file/list (files/internal (app/files app) folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (file/directory? file)
                                   (recur (concat remaining (file/list file)) result)

                                   (extensions (file/extension file))
                                   (recur remaining (conj result (file/path file)))

                                   :else
                                   (recur remaining result))))]
             [path (texture/create path)])))
