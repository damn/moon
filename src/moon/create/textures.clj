(ns moon.create.textures
  (:require [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.string :as str]))

(defn step [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures
         (let [{:keys [folder extensions]} {:folder "resources/"
                                            :extensions #{"png" "bmp"}}]
           (into {} (for [path (map (fn [path]
                                      (str/replace-first path folder ""))
                                    (file-handle/recursively-search (files/internal files folder) extensions))]
                      [path (texture/create path)])))))
