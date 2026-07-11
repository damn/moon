(ns moon.file-handle
  (:require [com.badlogic.gdx.files.file-handle :as file-handle]))

(defn recursively-search [handle extensions]
  (loop [[handle & remaining] (file-handle/list handle)
         result []]
    (cond (nil? handle)
          result

          (file-handle/isDirectory handle)
          (recur (concat remaining (file-handle/list handle)) result)

          (extensions (file-handle/extension handle))
          (recur remaining (conj result (file-handle/path handle)))

          :else
          (recur remaining result))))
