(ns gdx.file-handle
  (:import (com.badlogic.gdx.files FileHandle)))

(defn recursively-search [handle extensions]
  (loop [[handle & remaining] (.list ^FileHandle handle)
         result []]
    (cond (nil? handle)
          result

          (.isDirectory ^FileHandle handle)
          (recur (concat remaining (.list ^FileHandle handle)) result)

          (extensions (.extension ^FileHandle handle))
          (recur remaining (conj result (.path ^FileHandle handle)))

          :else
          (recur remaining result))))
