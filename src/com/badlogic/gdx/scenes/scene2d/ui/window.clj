(ns com.badlogic.gdx.scenes.scene2d.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defn create
  [{:keys [title skin]}]
  (Window. ^String title ^Skin skin))

(defn set-modal! [window modal?]
  (Window/.setModal window modal?))

(def title-table Window/.getTitleTable)
