(ns ctx.textures
  (:require [clojure.string :as str]
            [clojure.gdx :as gdx]))

(defn step
  [{:keys [ctx/files]}
   {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (gdx/recursively-search (gdx/internal files folder) extensions))]
             [path (gdx/texture (gdx/internal files path))])))
