(ns moon.create.textures
  (:require [moon.files :as files-utils])
  (:import (com.badlogic.gdx.graphics Texture)))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   folder]
  (assoc ctx :ctx/textures
         (into {} (for [path (files-utils/search files folder)]
                    [path (Texture. ^String path)]))))
