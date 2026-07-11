(ns gdx.file
  (:require [com.badlogic.gdx.files.file-handle :as file]))

(defn recursively-search [file extensions]
  (loop [[file & remaining] (file/list file)
         result []]
    (cond (nil? file)
          result

          (file/isDirectory file)
          (recur (concat remaining (file/list file)) result)

          (extensions (file/extension file))
          (recur remaining (conj result (file/path file)))

          :else
          (recur remaining result))))
