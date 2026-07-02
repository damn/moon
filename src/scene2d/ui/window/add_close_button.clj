(ns scene2d.ui.window.add-close-button
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.remove :as remove]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.table.add-cell :refer [add-cell!]]
            [scene2d.ui.text-button :as text-button])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f! [window skin]
  (add-cell! (Window/.getTitleTable window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (add-listener/f (change-listener/create
                                       (fn [_event _actor]
                                         (remove/f window)))))}))
