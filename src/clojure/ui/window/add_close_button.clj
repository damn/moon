(ns clojure.ui.window.add-close-button
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (window/getTitleTable window)
             {:actor (doto (text-button/new "X" skin)
                       (actor/addListener (change-listener/create
                                       (fn [_event _actor]
                                         (actor/remove window)))))}))
