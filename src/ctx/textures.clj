(ns ctx.textures
  (:require [clojure.string :as str]
            [files.internal :as internal]
            [gdx.files.file-handle :as file]
            [file-handle.texture :as texture]))

(defn step
  [{:keys [ctx/files]}
   {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[file & remaining] (file/list (internal/f files folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (file/directory? file)
                                   (recur (concat remaining (file/list file)) result)

                                   (extensions (file/extension file))
                                   (recur remaining (conj result (file/path file)))

                                   :else
                                   (recur remaining result))))]
             [path (texture/f (internal/f files path))])))
