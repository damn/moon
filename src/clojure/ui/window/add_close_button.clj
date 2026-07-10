(ns clojure.ui.window.add-close-button
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.ui-text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (window/getTitleTable window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (actor/addListener (change-listener/create
                                       (fn [_event _actor]
                                         (actor/remove window)))))}))
