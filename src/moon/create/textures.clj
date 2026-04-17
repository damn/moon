(ns moon.create.textures
  (:require [clojure.gdx.texture :as texture]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.texture :as gdl-texture]
            [clojure.string :as str]
            [moon.textures :as textures]))

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

(extend-type clojure.lang.PersistentHashMap
  textures/Textures
  (texture-region [textures {:keys [image/file image/bounds]}]
    (assert file)
    (assert (contains? textures file))
    (let [texture (get textures file)]
      (if-let [[x y w h] bounds]
        (gdl-texture/region texture x y w h)
        (gdl-texture/region texture)))))
