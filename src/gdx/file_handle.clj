(ns gdx.file-handle
  (:require [com.badlogic.gdx.files.file-handle :as file-handle]))

(defn recursively-search [file-handle extensions]
  (loop [[file & remaining] (file-handle/list-files file-handle)
         result []]
    (cond (nil? file)
          result

          (file-handle/directory? file)
          (recur (concat remaining (file-handle/list-files file)) result)

          (extensions (file-handle/extension file))
          (recur remaining (conj result (file-handle/path file)))

          :else
          (recur remaining result))))
