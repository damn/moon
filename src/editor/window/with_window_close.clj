(ns editor.window.with-window-close
  (:require [clojure.gdx.actor.get-stage :as get-stage]
            [clojure.gdx.actor.remove :as remove]
            [clojure.gdx.stage.add-actor :as add-actor]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]
            [scene2d.actor.find-ancestor :refer [find-ancestor]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (get-stage/f actor)]
       (set! (.ctx stage) new-ctx))
     (remove/f (find-ancestor actor #(instance? Window %)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (add-actor/f stage
                    (error-window/create
                     {:type :ui/error-window
                      :skin skin
                      :throwable t}))))))
