(ns screens.game
  (:require [core.component :as component]
            [api.context :as ctx]
            [api.screen :as screen :refer [Screen]]
            context.ui.actors))

(defrecord SubScreen []
  Screen
  (show [_ _context])

  (hide [_ ctx]
    (ctx/set-cursork! ctx :cursors/default))

  (render [_ ctx]
    (ctx/render-game ctx)))

(component/def :screens/game {}
  _
  (screen/create [_ ctx]
    (ctx/->stage-screen ctx
                        {:actors (context.ui.actors/->ui-actors ctx)
                         :sub-screen (->SubScreen)})))
