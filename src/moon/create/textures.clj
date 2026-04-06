(ns moon.create.textures
  (:require [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [clj.api.com.badlogic.gdx.graphics.texture :as texture]
            [clojure.string :as str]))

(defn- recursively-search
  [folder extensions]
  (loop [[file & remaining] (file-handle/list folder)
         result []]
    (cond (nil? file)
          result

          (file-handle/directory? file)
          (recur (concat remaining (file-handle/list file)) result)

          (extensions (file-handle/extension file))
          (recur remaining (conj result (file-handle/path file)))

          :else
          (recur remaining result))))

(defn- search [files {:keys [folder extensions]}]
  (map (fn [path]
         (str/replace-first path folder ""))
       (recursively-search (files/internal files folder) extensions)))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   folder]
  (assoc ctx :ctx/textures
         (into {} (for [path (search files folder)]
                    [path (texture/create path)]))))
