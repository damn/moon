(ns editor.window.with-window-close
  (:require
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.stage.add-actor :as add-actor]
            [clojure.gdx.stage.set-ctx :as set-ctx]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (actor/get-stage actor)]
       (set-ctx/f stage new-ctx))
     (actor/remove! (find-ancestor actor window?/f))
     (catch Throwable t
       (throwable/pretty-pst t)
       (add-actor/f stage
                    (error-window/create
                     {:type :ui/error-window
                      :skin skin
                      :throwable t}))))))
