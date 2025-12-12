(ns moon.files
  (:require [clojure.string :as str]
            [gdl.files :as files]
            [gdl.files.file-handle :as fh]))

(defn- recursively-search
  [folder extensions]
  (loop [[file & remaining] (fh/list folder)
         result []]
    (cond (nil? file)
          result

          (fh/directory? file)
          (recur (concat remaining (fh/list file)) result)

          (extensions (fh/extension file))
          (recur remaining (conj result (fh/path file)))

          :else
          (recur remaining result))))

(defn search [files {:keys [folder extensions]}]
  (map (fn [path]
         (str/replace-first path folder ""))
       (recursively-search (files/internal files folder) extensions)))
