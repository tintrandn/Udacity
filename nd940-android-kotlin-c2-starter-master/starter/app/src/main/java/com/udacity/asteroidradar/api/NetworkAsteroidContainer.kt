package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.database.AsteroidEntity

data class NetworkAsteroidContainer(val asteroids: List<Asteroid>)

fun NetworkAsteroidContainer.asDatabaseModel(): Array<AsteroidEntity> {
    return asteroids.map {
        AsteroidEntity(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}