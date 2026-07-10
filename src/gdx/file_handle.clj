(ns gdx.file-handle
  (:require [com.badlogic.gdx.files.file-handle :as file-handle]))

; TODO name suggestion: com.badlogic.gdx.files.file-handle.recursively-search/f
; ??
; or gdx.files.file-handle
; or gdx.file-handle
; or 'clojure.file-handle' (we are now in clojure land are we?) idk
; 'clojure.file-handle.recursively-search/f'
; or _DIRECTLY INLINE_ ????
; but this is a separate form
; or as public fn in create textures?
; also not!

(defn recursively-search [file-handle extensions]
  (loop [[file & remaining] (file-handle/list file-handle)
         result []]
    (cond (nil? file)
          result

          (file-handle/isDirectory file)
          (recur (concat remaining (file-handle/list file)) result)

          (extensions (file-handle/extension file))
          (recur remaining (conj result (file-handle/path file)))

          :else
          (recur remaining result))))
