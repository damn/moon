(ns moon.create.skin
  (:require [clojure.gdx.skin :as skin]
            [clojure.files :as files]))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   path]
  (assoc ctx :ctx/skin (skin/create (files/internal files path))))
