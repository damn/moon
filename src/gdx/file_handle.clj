(ns gdx.file-handle
  (:require [com.badlogic.gdx.files.file-handle :as fh]))

(defn recursively-search [file-handle extensions]
  (loop [[file-handle & remaining] (fh/list file-handle)
         result []]
    (cond (nil? file-handle)
          result

          (fh/isDirectory file-handle)
          (recur (concat remaining (fh/list file-handle)) result)

          (extensions (fh/extension file-handle))
          (recur remaining (conj result (fh/path file)))

          :else
          (recur remaining result))))
