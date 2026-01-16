(ns moon.create.skin
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   path]
  (assoc ctx :ctx/skin (let [skin (Skin. (.internal files path))]
                         (set! (.markupEnabled (-> skin (.getFont "default-font") .getData))
                               true)
                         skin)))
