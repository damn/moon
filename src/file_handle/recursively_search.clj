(ns file-handle.recursively-search
  (:import (com.badlogic.gdx.files FileHandle)))

(defn f [^FileHandle file-handle extensions]
  (loop [[file & remaining] (.list file-handle)
         result []]
    (cond (nil? file)
          result

          (.isDirectory ^FileHandle file)
          (recur (concat remaining (.list ^FileHandle file)) result)

          (extensions (.extension ^FileHandle file))
          (recur remaining (conj result (.path ^FileHandle file)))

          :else
          (recur remaining result))))
