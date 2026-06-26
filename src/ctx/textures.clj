(ns ctx.textures
  (:require [clojure.string :as str]
            [com.badlogic.gdx.graphics.texture :as texture]
            [files.internal :as internal]
            [file-handle.recursively-search :as recursively-search]))

(defn step
  [{:keys [ctx/files]}
   {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (internal/f files folder) extensions))]
             [path (texture/create (internal/f files path))])))
