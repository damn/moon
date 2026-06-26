(ns file-handle.recursively-search
  (:require [com.badlogic.gdx.files.file-handle :as file]))

(defn f [file-handle extensions]
  (loop [[file & remaining] (file/list file-handle)
         result []]
    (cond (nil? file)
          result

          (file/directory? file)
          (recur (concat remaining (file/list file)) result)

          (extensions (file/extension file))
          (recur remaining (conj result (file/path file)))

          :else
          (recur remaining result))))
