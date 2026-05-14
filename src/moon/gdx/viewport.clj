(ns moon.gdx.viewport
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [moon.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create [width height camera]
  ; ScalingViewport Scaling.fit
  (proxy [FitViewport ILookup] [width height camera]
    (valAt [k]
      (case k
        :viewport/camera (.getCamera this)
        ))))

(extend-type FitViewport
  viewport/Viewport
  (world-width [viewport]
    (.getWorldWidth viewport))

  (world-height [viewport]
    (.getWorldHeight viewport))

  (update! [viewport screen-width screen-height center-camera?]

    ; from ScalingViewport

    ;	public void update (int screenWidth, int screenHeight, boolean centerCamera) {
    ;		Vector2 scaled = scaling.apply(getWorldWidth(), getWorldHeight(), screenWidth, screenHeight);
    ;		int viewportWidth = Math.round(scaled.x);
    ;		int viewportHeight = Math.round(scaled.y);
    ;
    ;		// Center.
    ;		setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
    ;
    ;		apply(centerCamera);
    ;	}

    (.update viewport screen-width screen-height center-camera?))

  (unproject [viewport position]

    ; from Viewport

    ;		tmp.set(touchCoords.x, touchCoords.y, 1);
    ;		camera.unproject(tmp, screenX, screenY, screenWidth, screenHeight);
    ;		touchCoords.set(tmp.x, tmp.y);
    ;		return touchCoords;

    (-> viewport
        (.unproject (vector2/->java position))
        vector2/->clj)))
