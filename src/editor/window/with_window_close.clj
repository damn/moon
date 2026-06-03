(ns editor.window.with-window-close
  (:require [clojure.gdx.scene2d.actor :refer [get-stage]]
            [clojure.gdx.scene2d.actor.remove :refer [remove!]]
            [clojure.gdx.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.gdx.scene2d.stage.set-ctx :refer [set-ctx!]]
            [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]
            [gdx.scenes.scene2d.ui :as ui]))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (get-stage actor)]
       (set-ctx! stage new-ctx))
     (remove! (find-ancestor actor ui/window?))
     (catch Throwable t
       (throwable/pretty-pst t)
       (add-actor! stage
                   (error-window/create
                    {:type :ui/error-window
                     :skin skin
                     :throwable t}))))))
